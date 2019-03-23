import { Component, OnInit } from '@angular/core';
import {ScaUserDataTO} from "../../../api/models/sca-user-data-to";
import {Router} from "@angular/router";

@Component({
  selector: 'app-sca-selection',
  templateUrl: './sca-selection.component.html',
  styleUrls: ['./sca-selection.component.scss']
})
export class ScaSelectionComponent implements OnInit {

  public scaMehods: ScaUserDataTO[] = [
    {id: '363671612', methodValue: 'anton.brueckner@gmail.com', scaMethod: 'EMAIL'},
    {id: '123456789', methodValue: '+491266373673763', scaMethod: 'MOBILE'} ];

      sorry: 0;
      success: 1;
  constructor(private router: Router) { }

  ngOnInit() {
  }

  public onCancel(): void {
      this.router.navigate( ['/result', this.sorry]);
  }

  public onNext(): void {
      this.router.navigate(['/result', this.success]); // please insert the next url on this line

  }
}
