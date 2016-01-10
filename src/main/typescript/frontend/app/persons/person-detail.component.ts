import {Component, OnChanges, OnInit }  from 'angular2/core';
import {DvdService} from '../dvds/dvd.service';
import {Person} from './person';
import {RouteParams, Router} from 'angular2/router';
import {PersonService} from './person.service';

@Component({
  selector:'person-detail',
  templateUrl: 'app/persons/person-detail.component.html',
  inputs:['person', 'personType']
})
export class PersonDetailComponent implements OnInit, OnChanges {
  
  public person: Person;
  public personType: string;
  
  constructor(
    private _dvdService: DvdService,
    private _personService: PersonService,
    private _router: Router,
    private _routeParams: RouteParams) {
  }

  ngOnChanges(changes) {
    if(this.person == null) {
      return;
    }
    this.getDvds(this.person, this.personType);
  }
  
  goToDvd(id: number | string) {
    this._router.navigate(['DvdDetail', {id:id}]);
  }
  
  ngOnInit() {
    var id = this._routeParams.get('id');
    this.personType = this._routeParams.get('personType')
    if(id == null) {
      return;
    }
    this.getPerson(id, this.personType)
    .subscribe(
      data => {
        this.person = data;
        this.getDvds(this.person, this.personType);
      },
      err => console.error(err)
    );
  }
  
  getDvds(person: Person, personType: string) {
    this._dvdService.getDvdsByPerson(person.id, personType)
    .subscribe(
      data => this.person.dvds = data,
      err => console.error(err)
    );
  }
  
  getPerson(id: number | string, personType: string) {
    if(personType == "author") {
      return this._personService.getAuthor(id);
    } else if(personType == 'director') {
      return this._personService.getDirector(id);
    }
  }
  
}
