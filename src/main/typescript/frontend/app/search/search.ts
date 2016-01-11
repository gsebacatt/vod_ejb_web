export interface Search {
  resource: any;
  id?: number;
  fields?: any[];
  parentResource?: Search;
}