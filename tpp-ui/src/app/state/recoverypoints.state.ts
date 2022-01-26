/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

import { State, Action, StateContext, Selector } from '@ngxs/store';
import { RecoveryPoint } from '../models/recovery-point.models';
import {
  AddRecoveryPoint,
  DeleteRecoveryPoint,
  GetRecoveryPoint,
  RollbackRecoveryPoint,
} from '../components/actions/revertpoints.action';
import { ResetLedgersService } from '../services/reset-ledgers.service';
import { tap } from 'rxjs/operators';
import { Injectable } from '@angular/core';

export class RecoveryPointStateModel {
  recoveryPoints: RecoveryPoint[];
  selectedPoints: RecoveryPoint;
}

@State<RecoveryPointStateModel>({
  name: 'recoveryPoints',
  defaults: {
    recoveryPoints: [],
    selectedPoints: null,
  },
})
@Injectable()
export class RecoveryPointState {
  constructor(private resetLedgersService: ResetLedgersService) {}

  @Selector()
  static getRecoveryPointsList(state: RecoveryPointStateModel) {
    return state.recoveryPoints;
  }

  @Action(GetRecoveryPoint)
  getRecoveryPoint({
    getState,
    setState,
  }: StateContext<RecoveryPointStateModel>) {
    return this.resetLedgersService.getAllRecoveryPoints().pipe(
      tap((result) => {
        const state = getState();
        setState({
          ...state,
          recoveryPoints: Object(result),
        });
      })
    );
  }

  @Action(AddRecoveryPoint)
  addRecoveryPoint(
    { getState, patchState }: StateContext<RecoveryPointStateModel>,
    { payload }: AddRecoveryPoint
  ) {
    return this.resetLedgersService.createRecoveryPoints(payload).pipe(
      tap((result) => {
        const state = getState();
        patchState({
          recoveryPoints: [...state.recoveryPoints, result],
        });
      })
    );
  }

  @Action(DeleteRecoveryPoint)
  deleteRecoveryPoint(
    { getState, setState }: StateContext<RecoveryPointStateModel>,
    { id }: DeleteRecoveryPoint
  ) {
    return this.resetLedgersService.deleteRecoveryPoints(id).pipe(
      tap(() => {
        const state = getState();
        const filteredArray = state.recoveryPoints.filter(
          (item) => item.id !== id
        );
        setState({
          ...state,
          recoveryPoints: filteredArray,
        });
      })
    );
  }

  @Action(RollbackRecoveryPoint)
  rollbackRecoveryPoint(
    { getState, setState }: StateContext<RecoveryPointStateModel>,
    { id }: RollbackRecoveryPoint
  ) {
    return this.resetLedgersService.rollBackPointsById(id).pipe(
      tap(() => {
        const state = getState();
        const filteredArray = state.recoveryPoints.filter(
          (item) => item.id !== id.recoveryPointId
        );
        setState({
          ...state,
          recoveryPoints: filteredArray,
        });
      })
    );
  }
}
