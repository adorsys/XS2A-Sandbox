import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../../../../services/json.service';

@Component({
  selector: 'app-funds-confirmation',
  templateUrl: './funds-confirmation.component.html',
  styleUrls: ['./funds-confirmation.component.scss'],
})
export class FundsConfirmationComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {};
  body;

  constructor(private jsonService: JsonService) {}

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit(): void {
    this.jsonService.getPreparedJsonData(this.jsonService.jsonLinks.fundsConfirmation).subscribe((data) => (this.body = data));
  }
}
