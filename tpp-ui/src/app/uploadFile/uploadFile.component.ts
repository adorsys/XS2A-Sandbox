import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FileItem} from 'ng2-file-upload';
import {environment} from '../../environments/environment';
import {UploadOptions} from '../services/upload.service';

@Component({
    selector: 'app-upload-file',
    templateUrl: './uploadFile.component.html',
    styleUrls: ['./uploadFile.component.css']
})
export class UploadFileComponent implements OnInit {

    private url = `${environment.staffAccessResourceEndPoint}`;
    public options: UploadOptions;

    constructor(private router: Router) {
    }

    public ngOnInit(): void {
        this.options = {
            method: 'PUT',
            url: this.url + '/upload',

            methodAfterSuccess: (item: FileItem, response: string) => {
                this.router.navigate(['/users/all']);
            }
        };
    }
}
