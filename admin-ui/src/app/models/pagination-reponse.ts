export class PaginationResponse<T> {
  content: T;
  firstPage: boolean;
  lastPage: boolean;
  nextPage: boolean;
  number: number;
  numberOfElements: number;
  pageable: boolean;
  previousPage: boolean;
  size: number;
  totalElements: number;
  totalPages: number;
}
