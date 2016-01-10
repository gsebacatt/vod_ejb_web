import {Dvd} from '../dvds/dvd';

export interface DvdProvider {
  id: number;
  name: string;
  dvds?: Dvd;
}