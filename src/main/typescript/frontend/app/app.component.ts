import {Component} from 'angular2/core';
import {RouteConfig,  ROUTER_DIRECTIVES} from 'angular2/router';
import {HTTP_PROVIDERS} from 'angular2/http';

import {DvdService} from './dvds/dvd.service';
import {DvdListComponent} from './dvds/dvd-list.component';
import {DvdDetailComponent} from './dvds/dvd-detail.component';

import {DvdOrderService} from './dvd-orders/dvd-order.service';
import {DvdOrderListComponent} from './dvd-orders/dvd-order-list.component';
import {DvdOrderDetailComponent} from './dvd-orders/dvd-order-detail.component';

import {PersonService} from './persons/person.service';
import {PersonDetailComponent} from './persons/person-detail.component';

@Component({
  selector: 'my-app',
  template: `
    <h1 class="title">Component Router</h1>
    <nav>
      <a [routerLink]="['Dvds']">Dvds</a>
      <a [routerLink]="['DvdOrders']">Dvd orders</a>
      <a *ngIf="getCart()" [routerLink]="['DvdOrderDetail', {id: getCart().id}]">Cart ({{getItemsNbr()}})</a>
    </nav>
    <router-outlet></router-outlet>
  `,
  providers:  [DvdService, DvdOrderService, PersonService, HTTP_PROVIDERS],
  directives: [ROUTER_DIRECTIVES]
})
@RouteConfig([
  {path: '/dvd', name: 'Dvds', component: DvdListComponent, useAsDefault: true},
  {path: '/dvd/:id', name: 'DvdDetail', component: DvdDetailComponent},
  {path: '/person/:personType/:id', name: 'PersonDetail', component: PersonDetailComponent},
  {path: '/dvd_orders', name: 'DvdOrders', component: DvdOrderListComponent},
  {path: '/dvd_orders/:id', name: 'DvdOrderDetail', component: DvdOrderDetailComponent},
])
export class AppComponent { 
  
  constructor(
    private _dvdOrderService: DvdOrderService
  ) {}

  getCart() {
    return this._dvdOrderService.getCart();
  }
  
  getItemsNbr() {
    return this._dvdOrderService.getItemsNbr();
  }

}
