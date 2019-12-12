import {Component, OnInit} from '@angular/core';
import {environment} from '../../environments/environment';
import {UploadOptions} from '../services/upload.service';
import {TestDataGenerationService} from "../services/test.data.generation.service";
import {InfoService} from "../commons/info/info.service";

@Component({
  selector: 'app-upload-file',
  templateUrl: './uploadFile.component.html',
  styleUrls: ['./uploadFile.component.scss']
})
export class UploadFileComponent implements OnInit {

  uploadDataConfigs: UploadOptions[];
  private url = `${environment.tppBackend}`;
  private message = 'Test data has been successfully generated.';

  constructor(private generationService: TestDataGenerationService,
              private infoService: InfoService
  ) {
  }

  public ngOnInit(): void {
    this.uploadDataConfigs = [
      {
        exampleFileName: 'Users-Accounts-Balances-Payments-Example.yml',
        title: 'Upload Users/Accounts/Balances/Payments',
        method: 'PUT',
        url: this.url + '/data/upload',
        exampleFileUrl: '/accounts/example'
      },
      {
        exampleFileName: 'Consents-Example.yml',
        title: 'Upload Consents',
        method: 'PUT',
        url: this.url + '/consent',
        exampleFileUrl: '/consent/example',
      },
      {
        exampleFileName: 'Transactions-Example.csv',
        title: 'Upload Transactions',
        method: 'PUT',
        url: this.url + '/data/upload/transactions',
        exampleFileUrl: '/data/example'
      }
    ];
  }

  generateFileExample(uploadDataConfig) {
    return this.generationService.generateExampleTestData(uploadDataConfig.exampleFileUrl)
      .subscribe(data => {
        this.infoService.openFeedback(this.message);
        const blob = new Blob([data], {type: 'plain/text'});
        let link = document.createElement("a");
        link.setAttribute("href", window.URL.createObjectURL(blob));
        link.setAttribute("download", uploadDataConfig.exampleFileName);
        document.body.appendChild(link);
        link.click();
      });
  }
}
