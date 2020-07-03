import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { RecoveryPoint } from '../models/recovery-point.models';

@Injectable({
  providedIn: 'root',
})
export class ResetLedgersService {
  private url = `${environment.tppBackend}`;

  constructor(private http: HttpClient) {}

  public getAllRecoveryPoints() {
    return this.http.get<RecoveryPoint>(this.url + '/recovery/points');
  }

  public rollBackPointsById(data: any) {
    return this.http.post(this.url + `/revert`, data);
  }

  public createRecoveryPoints(point: RecoveryPoint) {
    return this.http.post<RecoveryPoint>(this.url + `/recovery/point`, point);
  }

  public deleteRecoveryPoints(id: string) {
    return this.http.delete<RecoveryPoint>(this.url + `/recovery/point/${id}`);
  }
}
