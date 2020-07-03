import { Component, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { RecoveryPoint } from '../../models/recovery-point.models';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ResetLedgersService } from '../../services/reset-ledgers.service';
import { InfoService } from '../../commons/info/info.service';
import { BsModalService } from 'ngx-bootstrap';
import {
  AddRecoveryPoint,
  GetRecoveryPoint,
} from '../actions/revertpoints.action';
import { Select, Store } from '@ngxs/store';
import { RecoveryPointState } from '../../state/recoverypoints.state';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss'],
})
export class ModalComponent implements OnInit {
  @Select(RecoveryPointState.getRecoveryPointsList)
  ngxsrecoveryPoint: Observable<RecoveryPoint[]>;
  public title: string;
  public list: Array<string>;
  public closeBtnName: string;
  public userForm: FormGroup;

  constructor(
    public modal: BsModalService,
    private ledgersService: ResetLedgersService,
    private infoService: InfoService,
    private store: Store,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.createForm();
  }

  createForm() {
    this.userForm = this.fb.group({
      description: [''],
    });
  }

  getRecoveryPoints() {
    this.store.dispatch(new GetRecoveryPoint());
  }

  createRecoveryPoints(): void {
    const createRecoveryPoint: RecoveryPoint = {
      id: '',
      description: this.userForm.get('description').value,
      rollBackTime: '',
      branchId: '',
    };
    this.store
      .dispatch(new AddRecoveryPoint(createRecoveryPoint))
      .subscribe(() => this.getRecoveryPoints());
    this.infoService.openFeedback('State successfully created');
    this.close();
  }

  close(): void {
    this.modal._hideModal(1);
  }
}
