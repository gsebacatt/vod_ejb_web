import {Component, OnInit}   from 'angular2/core';
import {DvdOrder} from './dvd-order';
import {DvdOrderService} from './dvd-order.service';
import {Router, RouteParams} from 'angular2/router';

@Component({
  templateUrl: 'app/dvd-orders/dvd-order-list.component.html',
})
export class DvdOrderListComponent implements OnInit {
  public parentDvdOrders: DvdOrder[];
    
  constructor(
    private _dvdOrderService: DvdOrderService,
    private _router: Router,
    routeParams: RouteParams) {
  }
      
  goToDvdOrder(id: number | string) {
    this._router.navigate(['DvdOrderDetail', {id: id}]);
  }
      
  ngOnInit() {
    this._dvdOrderService.getDvdOrders()
    .subscribe(
      data => {
        this.parentDvdOrders = data.filter(item => item.parentDvdOrder == undefined);
      },
      err => console.log(err),
      () => console.log('DvdOrderListComponent initiated')      
    );
  }
  
}