export interface DvdOrder {
  id:number;
  price: number;
  externalState: string;
  created: number;
  updated: number;
  parentDvdOrder?: DvdOrder;
  dvdOrderDvds?: any;
  subDvdOrders?: DvdOrder;
}
