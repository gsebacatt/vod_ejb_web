import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {Search} from './search';
import 'rxjs/Rx';

@Injectable()
export class SearchService {
  
  private url;
  
  constructor(private _http: Http) {
    this.url = 'http://localhost:8080/vod-2/api/search';
  }

  createSearch(search: Search) {
    return this._http.post(
      this.url,
      JSON.stringify(search)
    ).map( res => res.json() );
  }
  
}
