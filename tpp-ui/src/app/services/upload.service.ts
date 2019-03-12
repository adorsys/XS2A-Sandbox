import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';
import {ParsedResponseHeaders} from 'ng2-file-upload/file-upload/file-uploader.class';
import 'rxjs/add/operator/map';
import {environment} from '../../environments/environment';


@Injectable({
    providedIn: 'root'
})
export class UploadService {

    private url = `${environment.staffAccessResourceEndPoint}`;

    constructor(protected http: HttpClient) {
    }

    createInstanceFileUploader(options: UploadOptions): FileUploader {
        // Set the current user credentials in Headers for Basic Authorization
        const headers = [];
        const token = 'xxxx';
        if (token) {
            const header = {name: 'Authorization', value: 'Basic ' + token};
            headers.push(header);
        }

        return new FileUploader(
            {
                url: this.url,
                method: 'POST',
                headers,
                allowedMimeType: options.allowedMimeType
            });
    }

}

export interface UploadOptions {
    method: string;
    url: string;
    queueLimit?: number;
    allowedMimeType?: string[];
    allowedFileType?: string[];
    context?: string;

    methodAfterSuccess(item?: FileItem, response?: string, status?: number, headers?: ParsedResponseHeaders): any;
}
