/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

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
