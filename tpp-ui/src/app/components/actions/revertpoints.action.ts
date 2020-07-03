import { RecoveryPoint } from '../../models/recovery-point.models';

export class AddRecoveryPoint {
  static readonly type = '[RecoveryPoint] Add';
  constructor(public payload: RecoveryPoint) {}
}

export class GetRecoveryPoint {
  static readonly type = '[RecoveryPoint] Get';
}

export class DeleteRecoveryPoint {
  static readonly type = '[RecoveryPoint] Delete';
  constructor(public id: string) {}
}
