import { Component, Input, OnInit } from '@angular/core';
import { RestService } from '../../../../services/rest.service';
import { DataService } from '../../../../services/data.service';

@Component({
  selector: 'app-play-wth-data',
  templateUrl: './play-wth-data.component.html',
  styleUrls: ['./play-wth-data.component.scss'],
})
export class PlayWthDataComponent implements OnInit {
  @Input() method: string;
  @Input() headers: object;
  @Input() body: object;
  @Input() url: string;
  response: object = {};

  constructor(
    public restService: RestService,
    public dataService: DataService
  ) {}

  sendRequest() {
    this.dataService.isLoading = true;

    // TODO check if request has body or make switch case for get, post, delete and put

    const respBodyEl = document.getElementById('textArea');
    if (this.isValidJSONString(respBodyEl['value'])) {
      const bodyValue = JSON.parse(respBodyEl['value']);
      this.restService
        .sendRequest(this.method, this.url, bodyValue, this.headers)
        .subscribe(
          resp => {
            this.response = Object.assign(resp);
            this.dataService.isLoading = false;
            this.dataService.showToast('Request sent', 'Success!', 'success');
            console.log('response:', this.response);
          },
          err => {
            this.dataService.isLoading = false;
            this.dataService.showToast(
              'Something went wrong!',
              'Error!',
              'error'
            );
            this.response = Object.assign(err);
            console.log('err', err);
          }
        );
    } else {
      this.dataService.isLoading = false;
      this.dataService.showToast('Body in not valid!', 'Error!', 'error');
    }
  }

  // Check if text in body in JSON format
  isValidJSONString(str) {
    try {
      JSON.parse(str);
    } catch (e) {
      return false;
    }
    return true;
  }

  // Fixing the loss of input focus
  trackByFn(index: any, item: any) {
    return index;
  }

  ngOnInit() {}
}
