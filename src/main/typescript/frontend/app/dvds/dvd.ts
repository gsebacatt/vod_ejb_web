import {DvdProvider} from '../dvd-providers/dvd-provider';
import {Person} from '../persons/person';

export interface Dvd {
    id: number;
    title: string;
    dvdProvider: DvdProvider;
    quantity: number;
    price: number;
    authors?: Person;
    directors?: Person;
}