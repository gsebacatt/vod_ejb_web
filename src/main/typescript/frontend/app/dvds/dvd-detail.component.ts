import {Component, OnChanges, OnInit }  from 'angular2/core';
import {Dvd} from './dvd';
import {RouteParams, Router} from 'angular2/router';
import {PersonService} from '../persons/person.service';
import {DvdService} from './dvd.service';

@Component({
  selector:'dvd-detail',
  templateUrl: 'app/dvds/dvd-detail.component.html',
  inputs:['dvd']
})
export class DvdDetailComponent implements OnInit, OnChanges {  
  public dvd: Dvd;
  
  constructor(
      private _dvdService: DvdService,
      private _personService: PersonService,
      private _router: Router,
      private _routeParams: RouteParams) {
    }
  
  ngOnChanges(changes) {
    this.getDvdInfos(this.dvd);
  }
  
  ngOnInit() {
    var id = this._routeParams.get('id');
    if(id == null) {
      return;
    }
    this._dvdService.getDvd(id)
    .subscribe(
      data => {
        this.dvd = data;
        this.getDvdInfos(this.dvd); 
      },
      err => console.error(err)
    );

  }
  
  goToPerson(id: number | string, personType: string) {
    this._router.navigate(['PersonDetail', {personType: personType, id: id}])
  }
  
  getDvdInfos(dvd: Dvd) {
    if(dvd == null) {
      return;
    }
    this._personService.getAuthorsByDvd(dvd.id)
    .subscribe(
      data => this.dvd.authors = data,
      err => console.error(err)
    );
    this._personService.getDirectorsByDvd(dvd.id)
    .subscribe(
      data => this.dvd.directors = data,
      err => console.error(err)
    );
  }
  
}