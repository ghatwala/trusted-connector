/*-
 * ========================LICENSE_START=================================
 * camel-ids
 * %%
 * Copyright (C) 2018 Fraunhofer AISEC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package de.fhg.aisec.ids.camel.ids;

import de.fhg.aisec.ids.api.conm.IDSCPIncomingConnection;
import de.fhg.aisec.ids.api.conm.IDSCPOutgoingConnection;
import de.fhg.aisec.ids.api.conm.RatResult;
import de.fhg.aisec.ids.camel.ids.connectionmanagement.ConnectionManagerService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.List;

public class IdsClientServerPlaintextWithAttestationTest extends CamelTestSupport {
  protected static final String TEST_MESSAGE = "Hello World!";
  protected static final String TEST_MESSAGE_2 = "Hello Again!";

  @Test
  public void testFromRouteAToB() throws InterruptedException {
    ConnectionManagerService conm = new ConnectionManagerService();
    assertTrue(conm.listIncomingConnections().isEmpty());

    MockEndpoint mock = getMockEndpoint("mock:result");

    // Send a test message into begin of client route
    template.sendBody("direct:input", TEST_MESSAGE);

    log.trace("mock is satisfied");

    // We expect that mock endpoint is happy and has received a message
    mock.assertIsSatisfied();
    mock.expectedBodiesReceived(TEST_MESSAGE);

    // We expect one incoming connection to be listed by ConnectionManager
    List<IDSCPIncomingConnection> incomings = conm.listIncomingConnections();
    assertEquals(1, incomings.size());

    IDSCPIncomingConnection incomingConnection = incomings.get(0);

    // We expect attestation to FAIL because unit tests have no valid TPM
    RatResult ratResult = incomingConnection.getAttestationResult();
    assertEquals(RatResult.Status.FAILED, ratResult.getStatus());

    // We expect some meta data about the remot endpoint
    assertEquals( "{\"message\":\"No InfomodelManager loaded\"}", incomingConnection.getMetaData());

    List<IDSCPOutgoingConnection> outgoings = conm.listOutgoingConnections();
    assertEquals(1, outgoings.size());

    // Also outgoing connection will have failed remote attestation
    IDSCPOutgoingConnection outgoingConnection = outgoings.get(0);
    assertEquals(RatResult.Status.FAILED, outgoingConnection.getAttestationResult().getStatus());

    // ... and some meta data
    String meta = outgoingConnection.getMetaData();
    assertEquals("{\"message\":\"No InfomodelManager loaded\"}",meta);
  }

  @Test
  public void testTwoRoutesRestartConsumer() throws Exception {
    MockEndpoint mock = getMockEndpoint("mock:result");
    resetMocks();

    // Send a message
    template.sendBody("direct:input", TEST_MESSAGE);

    mock.expectedBodiesReceived(TEST_MESSAGE);
    mock.assertIsSatisfied();

    // Clean the mocks
    resetMocks();

    // Now stop and start the client route
    log.info("Restarting client route");
    context.stopRoute("client");
    context.startRoute("client");

    template.sendBody("direct:input", TEST_MESSAGE_2);
    mock.expectedBodiesReceived(TEST_MESSAGE_2);
    mock.assertIsSatisfied();
  }

  @Override
  protected RouteBuilder[] createRouteBuilders() {
    return new RouteBuilder[] {
        new RouteBuilder() {
          public void configure() {
            from("direct:input").routeId("client").to("idsclientplain://localhost:9292/zero");
          }
        },
        new RouteBuilder() {
          public void configure() {
            from("idsserver://0.0.0.0:9292/zero").routeId("server").to("mock:result");
          }
        }
    };
  }
}
