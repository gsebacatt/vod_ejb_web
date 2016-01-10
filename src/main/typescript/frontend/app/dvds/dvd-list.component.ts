import {Component, OnInit}   from 'angular2/core';
import {Dvd} from './dvd';
import {DvdService} from './dvd.service';
import {Router, RouteParams} from 'angular2/router';
import {DvdDetailComponent} from './dvd-detail.component';

@Component({
  templateUrl: 'app/dvds/dvd-list.component.html',
  directives:[DvdDetailComponent]
})
export class DvdListComponent implements OnInit {
  public dvds: Dvd[];
  public selectedDvd: Dvd;
  
  constructor(
    private _dvdService: DvdService,
    private _router: Router,
    routeParams: RouteParams) {
  }
  
  onSelect(dvd: Dvd) {
    this.selectedDvd = dvd;
  }
    
  ngOnInit() {
    this._dvdService.getDvds()
    .subscribe(
      data => this.dvds = data,
      err => console.log(err),
      () => console.log('DvdListComponent initiated')      
    );
  }
  
}