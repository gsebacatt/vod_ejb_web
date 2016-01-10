import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {Dvd} from './dvd';
import 'rxjs/Rx';

@Injectable()
export class DvdService {
    
  constructor(private _http: Http) {}
      
  getDvds() {
    return this._http.get('http://localhost:8080/vod-2/api/dvd')
    .map( res => res.json() );
  }
  
  getDvdsByPerson(personId: number | string, personType: string) {
    return this._http.get('http://localhost:8080/vod-2/api/'+personType+'/'+personId+'/dvd')
    .map( res => res.json() );
  }
  
  getDvd(id: number | string) {
    return this._http.get('http://localhost:8080/vod-2/api/dvd/' + id)
    .map( res => res.json() );    
  }
  
}
