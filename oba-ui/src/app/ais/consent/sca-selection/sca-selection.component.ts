import { Component, OnInit } from '@angular/core';
import {ScaUserDataTO} from "../../../../../api/models/sca-user-data-to";

@Component({
  selector: 'app-sca-selection',
  templateUrl: './sca-selection.component.html',
  styleUrls: ['./sca-selection.component.scss']
})
export class ScaSelectionComponent implements OnInit {

  public scaMehods: ScaUserDataTO[] = [
    {id: '363671612', methodValue: 'anton.brueckner@gmail.com', scaMethod: 'EMAIL'},
    {id: '123456789', methodValue: '+491266373673763', scaMethod: 'MOBILE'} ];

  constructor() { }

  ngOnInit() {
  }

  public onCancel(): void {

  }

  public onNext(): void {

  }

}
