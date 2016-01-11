import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import 'rxjs/Rx';

@Injectable()
export class DvdOrderService {
  private url: string;
  
  constructor(private _http: Http) {
    this.url = 'http://localhost:8080/vod-2/api/dvd_order';
  }

  getDvdOrders() {
    return this._http.get(this.url)
    .map(res => res.json());
  }
  
  getDvdOrder(id: number | string) {
    return this._http.get(this.url + '/' + id)
    .map(res => res.json());
  }

  addDvd(orderId: number | string, dvd: any) {
    return this._http.post(
      this.url + '/' + orderId,
      JSON.stringify(dvd)
    ).map(res => res.json());
  }
  
  createDvdOrder() {
    return this._http.post(this.url, "")
    .map(res => res.json());
  }
  
  payDvdOrder(id: number | string) {
    return this._http.post(this.url + '/' + id + '/payment', "")
    .map(res => res.json());
  }
  
  getSubOrders(id: number | string) {
    return this._http.get(this.url + '/' + id + '/sub_dvd_order')
    .map(res => res.json());
  }
  
  getDvdOrderDvds(id: number | string) {
    return this._http.get(this.url + '/' + id + '/dvd_order_dvd')
    .map(res => res.json());
  }
  
}
