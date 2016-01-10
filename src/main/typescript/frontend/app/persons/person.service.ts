import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {Person} from './person';
import 'rxjs/Rx';

@Injectable()
export class PersonService {
  
  constructor(private _http: Http) {}
  
  getDirector(id: number | string) {
    return this._http.get('http://localhost:8080/vod-2/api/director/' + id)
    .map( res => res.json() );    
  }
  
  getAuthor(id: number | string) {
    return this._http.get('http://localhost:8080/vod-2/api/author/' + id)
    .map( res => res.json() );    
  }  
  
  getAuthors() {
    return this._http.get('http://localhost:8080/vod-2/api/author')
    .map( res => res.json() );
  }
  
  getDirectors() {
    return this._http.get('http://localhost:8080/vod-2/api/director')
    .map( res => res.json() );
  }
  
  getAuthorsByDvd(dvdId: number | string) {
    return this._http.get('http://localhost:8080/vod-2/api/dvd/'+dvdId+'/author').map( res => res.json() );
  }
  
  getDirectorsByDvd(dvdId: number | string) {
    return this._http.get('http://localhost:8080/vod-2/api/dvd/'+dvdId+'/director').map( res => res.json() );    
  }
  
}