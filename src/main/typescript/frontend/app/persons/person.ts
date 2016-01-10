import {Dvd} from '../dvds/dvd';

export interface Person {
  id: number;
  firstName: string;
  lastName: string;
  dvds?: Dvd;
}