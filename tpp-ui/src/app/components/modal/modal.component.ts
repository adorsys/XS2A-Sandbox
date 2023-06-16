/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

import { Component, OnInit } from '@angular/core';
import { RecoveryPoint } from '../../models/recovery-point.models';
import { UntypedFormGroup, UntypedFormBuilder } from '@angular/forms';
import { ResetLedgersService } from '../../services/reset-ledgers.service';
import { InfoService } from '../../commons/info/info.service';
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { AddRecoveryPoint, GetRecoveryPoint } from '../actions/revertpoints.action';
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
  public userForm: UntypedFormGroup;

  constructor(
    public modal: BsModalService,
    private ledgersService: ResetLedgersService,
    private infoService: InfoService,
    private store: Store,
    private fb: UntypedFormBuilder,
    private _bsModalRef: BsModalRef
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
    this.store.dispatch(new AddRecoveryPoint(createRecoveryPoint)).subscribe(() => this.getRecoveryPoints());
    this.infoService.openFeedback('State successfully created');
    this.close();
  }

  close(): void {
    this._bsModalRef.hide();
  }
}
