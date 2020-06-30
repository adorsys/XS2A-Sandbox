import { State, Action, StateContext, Selector } from '@ngxs/store';
import { RecoveryPoint } from '../models/recovery-point.models';
import {
  AddRecoveryPoint,
  DeleteRecoveryPoint,
  GetRecoveryPoint,
} from '../components/actions/revertpoints.action';
import { ResetLedgersService } from '../services/reset-ledgers.service';
import { tap } from 'rxjs/operators';

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
}
