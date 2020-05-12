import {User} from './user.model';

export interface TppQueryParams {
  userLogin?: string;
  tppId?: string;
  country?: string;
  tppLogin?: string;
  blocked?: boolean;
  ibanParam?: string;
}

export interface TppResponse {
  tpps: User[];
  totalElements: number;
}
