import {Injectable} from 'angular2/core';
import {Http, Headers} from 'angular2/http';
import 'rxjs/Rx';

@Injectable()
export class DvdOrderService {
  private url: string;
  private postHeaders: any;
  public cart: DvdOrder;
  public items: number;
  
  constructor(private _http: Http) {
    this.url = 'http://localhost:8080/vod-2/api/dvd_order/';
    this.postHeaders = new Headers();
    this.postHeaders.append('Content-Type', 'application/json');
    this.postHeaders.append('Accept', 'application/json');
  }


  getDvdOrders() {
    return this._http.get(this.url)
    .map(res => res.json());
  }
  
  getDvdOrder(id: number | string) {
    return this._http.get(this.url + id)
    .map(res => res.json());
  }

  addDvd(orderId: number | string, dvd: any) {
    console.log(JSON.stringify(dvd));
    return this._http.post(
      this.url + orderId + '/dvd',
      JSON.stringify(dvd),
      {headers: this.postHeaders}
    ).map(res => res.json());
  }
  
  createDvdOrder() {
    return this._http.post(this.url, JSON.stringify({}), {headers:this.postHeaders})
    .map(res => res.json());
  }
  
  payDvdOrder(id: number | string) {
    return this._http.post(this.url + id + '/payment', "", {headers:this.postHeaders})
    .map(res => res.json());
  }
  
  getSubOrders(id: number | string) {
    return this._http.get(this.url + id + '/sub_dvd_order')
    .map(res => res.json());
  }
  
  getDvdOrderDvds(id: number | string) {
    return this._http.get(this.url + id + '/dvd_order_dvd')
    .map(res => res.json());
  }

  addDvdToCart(id: number | string, quantity: number) {
    var dvd:Dvd = {"id": +id, "quantity": quantity};
    if(this.cart == null) {
      this.items = 0;
      this.createDvdOrder()
      .subscribe(
        data => {
          this.cart = data;
          this._addDvdToCart(this.cart.id, dvd);
        },
        err => console.error(err)
      );
    } else {
      this._addDvdToCart(id, dvd);
    }
    this.items++;
  }

  private _addDvdToCart(id: number | string,dvd: Dvd) {
    return this.addDvd(id, dvd).
    subscribe(
      data => {return;},
      err => console.error(err)
    );
  }

  getCart() {
    return this.cart;
  }
  
  getItemsNbr() {
    return this.items;
  }
  
}
