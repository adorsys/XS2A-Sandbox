import { Component, Input, OnInit } from '@angular/core';
import { RestService } from '../../../../services/rest.service';

@Component({
  selector: 'app-play-wth-data',
  templateUrl: './play-wth-data.component.html',
  styleUrls: ['./play-wth-data.component.scss'],
})
export class PlayWthDataComponent implements OnInit {
  @Input() method: string;
  @Input() headers: object;
  @Input() body: object;

  constructor(private restService: RestService) {}

  sendRequest() {
    this.restService.postRequest(this.body, this.headers).subscribe(
      resp => {
        console.log('resp', resp);
      },
      err => {
        console.log('err', err);
      }
    );
  }

  ngOnInit() {}
}
