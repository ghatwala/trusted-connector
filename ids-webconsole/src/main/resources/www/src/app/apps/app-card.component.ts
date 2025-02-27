import { Component, Input, OnInit } from '@angular/core';

import { App } from './app';
import { PortDef } from './app.port.def';
import { AppService } from './app.service';
import { AppsComponent } from './apps.component';

@Component({
    selector: 'app-card',
    templateUrl: './app-card.component.html'
})
export class AppCardComponent implements OnInit {
    @Input() public app: App;
    public statusIcon: string;
    public statusColor: string;
    private portDefs: Array<PortDef>;

    constructor(private readonly appService: AppService, private readonly appsComponent: AppsComponent) { }
    // the linter forces to mark 'appsComponent' as readonly which could be misleading; see method 'onDeleteBtnClick' below

    get ports(): Array<PortDef> {
        if (!this.portDefs) {
            this.portDefs = this.app.ports
                .map(list => list.split(',')
                    .map(portDef => new PortDef(portDef.trim())))
                .reduce((acc, x) => acc.concat(x), []);
        }

        return this.portDefs;
    }

    public trackPorts(_: number, item: PortDef): string {
        return item.text;
    }

    public ngOnInit(): void {
        if (this.app.status.indexOf('Up') >= 0) {
            this.statusIcon = 'stop';
            this.statusColor = '';
        } else {
            this.statusIcon = 'play_arrow';
            this.statusColor = 'card-dark';
        }
    }

    public onToggle(containerId: string): void {
        if (this.statusIcon === 'play_arrow') {
            this.statusIcon = 'stop';
            this.statusColor = '';
            this.appService.startApp(containerId)
              .subscribe(result => undefined);
            this.app.status = 'Up 1 seconds ago';
        } else {
            this.statusIcon = 'play_arrow';
            this.statusColor = 'card-dark';
            this.appService.stopApp(containerId)
              .subscribe(result => undefined);
            this.app.status = 'Exited(0) 1 seconds ago';
        }
    }

    public onDeleteBtnClick(containerId: string): void {
      this.appService.wipeApp(containerId)
        .subscribe(result => undefined);
      const index = this.appsComponent.apps.indexOf(this.app);
      this.appsComponent.apps.splice(index, 1) ;
    }
}
