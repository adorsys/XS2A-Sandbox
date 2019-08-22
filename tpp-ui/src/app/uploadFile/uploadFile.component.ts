import {Component, OnInit} from '@angular/core';
import {environment} from '../../environments/environment';
import {UploadOptions} from '../services/upload.service';

@Component({
    selector: 'app-upload-file',
    templateUrl: './uploadFile.component.html',
    styleUrls: ['./uploadFile.component.scss']
})
export class UploadFileComponent implements OnInit {

    public optionsData: UploadOptions;
    public optionsConsents: UploadOptions;
    public optionsTransactions: UploadOptions;
    private url = `${environment.tppBackend}`;

    public ngOnInit(): void {
        this.optionsData = {
            method: 'PUT',
            url: this.url + '/data/upload'
        };

        this.optionsConsents = {
            method: 'PUT',
            url: this.url + '/consent'
        };

        this.optionsTransactions = {
            method: 'PUT',
            url: this.url + '/data/upload/transactions'
        };
    }
}
