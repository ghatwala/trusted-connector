/*-
 * ========================LICENSE_START=================================
 * ids-comm
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
package de.fhg.aisec.ids.comm.ws.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Definition of FSM states for IDS protocol.
 *
 * @author Julian Schuette (julian.schuette@aisec.fraunhofer.de)
 * @author Georg Raess (georg.raess@aisec.fraunhofer.de)
 */
public final class ProtocolState {
  private String id;
  private String description;
  public final int ord;
  private static AtomicInteger upperBound = new AtomicInteger(0);

  /* -------------- States start here ------------ */
  public static final ProtocolState IDSCP_START =
      new ProtocolState("IDSCP:START", "Idscp: start of protocol.");
  public static final ProtocolState IDSCP_ERROR =
      new ProtocolState("IDSCP:ERROR", "Idscp: error in protocol.");
  public static final ProtocolState IDSCP_END =
      new ProtocolState("IDSCP:END", "Idscp: end of protocol.");
  public static final ProtocolState IDSCP_RAT_AWAIT_RESPONSE =
      new ProtocolState("IDSCP:RAT:AWAIT_RESPONSE", "Idscp/RemoteAttestation: awaiting response.");
  public static final ProtocolState IDSCP_RAT_AWAIT_REQUEST =
      new ProtocolState("IDSCP:RAT:AWAIT_REQUEST", "Idscp/RemoteAttestation: awaiting request.");
  public static final ProtocolState IDSCP_RAT_AWAIT_RESULT =
      new ProtocolState("IDSCP:RAT:AWAIT_RESULT", "Idscp/RemoteAttestation: awaiting result.");
  public static final ProtocolState IDSCP_RAT_AWAIT_LEAVE =
      new ProtocolState("IDSCP:RAT:AWAIT_LEAVE", "Idscp/RemoteAttestation: awaiting to leave.");
  public static final ProtocolState IDSCP_META_REQUEST =
      new ProtocolState("IDSCP:META:REQUEST", "Idscp/Metadataexchange: request.");
  public static final ProtocolState IDSCP_META_RESPONSE =
      new ProtocolState("IDSCP:META:RESPONSE", "Idscp/Metadataexchange: response.");
  /* -------------- States end here ------------ */

  private ProtocolState(String id, String description) {
    this.id = id;
    this.description = description;
    this.ord = upperBound.getAndIncrement();
  }

  public String id() {
    return this.id;
  }

  public String description() {
    return this.description;
  }

  public static int size() {
    return upperBound.get();
  }

  public String toString() {
    return this.description() + " (id: " + this.id() + ")";
  }
}
