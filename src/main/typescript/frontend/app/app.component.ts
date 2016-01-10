import {Component} from 'angular2/core';
import {RouteConfig,  ROUTER_DIRECTIVES} from 'angular2/router';
import {HTTP_PROVIDERS} from 'angular2/http';

import {DvdService} from './dvds/dvd.service';
import {DvdListComponent} from './dvds/dvd-list.component';
import {DvdDetailComponent} from './dvds/dvd-detail.component';

import {PersonService} from './persons/person.service';
import {PersonDetailComponent} from './persons/person-detail.component';

@Component({
  selector: 'my-app',
  template: `
    <h1 class="title">Component Router</h1>
    <nav>
      <a [routerLink]="['Dvds']">Dvds</a>
    </nav>
    <router-outlet></router-outlet>
  `,
  providers:  [DvdService, PersonService, HTTP_PROVIDERS],
  directives: [ROUTER_DIRECTIVES]
})
@RouteConfig([
  {path: '/dvd', name: 'Dvds', component: DvdListComponent, useAsDefault: true},
  {path: '/dvd/:id', name: 'DvdDetail', component: DvdDetailComponent},
  {path: '/person/:personType/:id', name: 'PersonDetail', component: PersonDetailComponent}
])
export class AppComponent { }
