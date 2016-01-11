import {Component, OnInit }  from 'angular2/core';
import {DvdOrder} from './dvd-order';
import {RouteParams, Router} from 'angular2/router';
import {DvdOrderService} from './dvd-order.service';

@Component({
  selector:'dvd-order-detail',
  templateUrl: 'app/dvd-orders/dvd-order-detail.component.html'
})
export class DvdOrderDetailComponent implements OnInit {  
  public dvdOrder: DvdOrder;
  public subDvdOrders: DvdOrder[];
  
  constructor(
      private _dvdOrderService: DvdOrderService,
      private _router: Router,
      private _routeParams: RouteParams) {
  }
    
  ngOnInit() {
    var id = this._routeParams.get('id');
    if(id == null) {
      return;
    }
    this._dvdOrderService.getDvdOrder(id)
    .subscribe(
      data => {
        this.dvdOrder = data;
        this._dvdOrderService.getDvdOrderDvds(data.id).
        subscribe(
          data => this.dvdOrder.dvdOrderDvds = data,
          err => console.error(err)
        );
      },
      err => console.error(err)
    );
    this._dvdOrderService.getSubOrders(id)
    .subscribe(
      data => {
        this.subDvdOrders = data;
        console.log(this.subDvdOrders);
        this.subDvdOrders.map(item => {
          this._dvdOrderService.getDvdOrderDvds(item.id).
          subscribe(
            data => {
              item.dvdOrderDvds = data;
              console.log(data);
            },
            err => console.error(err)
          );
        });
      },
      err => console.error(err)
    );
  }  
}