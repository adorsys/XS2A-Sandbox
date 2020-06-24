import { Component, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { RecoveryPoint } from '../../models/recovery-point.models';
import { FormControl, FormGroup } from '@angular/forms';
import { ResetLedgersService } from '../../services/reset-ledgers.service';
import { InfoService } from '../../commons/info/info.service';
import { BsModalService } from 'ngx-bootstrap';

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss'],
})
export class ModalComponent implements OnInit {
  public title: string;
  public list: Array<string>;
  public closeBtnName: string;
  public userForm: FormGroup;
  public description = new FormControl('');

  constructor(
    public modal: BsModalService,
    private ledgersService: ResetLedgersService,
    private infoService: InfoService
  ) {}

  ngOnInit() {
    this.userForm = new FormGroup({
      description: new FormControl(),
    });
  }

  createRecoveryPoints(): void {
    const createRecoveryPoint: RecoveryPoint = {
      id: '',
      description: this.userForm.get('description').value,
      rollBackTime: '',
      branchId: '',
    };
    this.ledgersService
      .createRecoveryPoints(createRecoveryPoint)
      .subscribe((point) => {
        this.infoService.openFeedback(' New Point successfully created');
      });
    this.userForm.patchValue({
      description: '',
    });
    this.close();
  }

  close(): void {
    this.modal._hideModal(1);
  }
}
