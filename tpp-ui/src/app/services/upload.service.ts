import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';
import {ParsedResponseHeaders} from 'ng2-file-upload/file-upload/file-uploader.class';
import {AuthService} from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class UploadService {
    constructor(protected http: HttpClient, private  auth: AuthService) {
    }

    createInstanceFileUploader(options: UploadOptions): FileUploader {
        // Set the current user credentials in Headers for Basic Authorization
        const headers = [];
        const header = {name: 'Authorization', value: 'Bearer '.concat(this.auth.getAuthorizationToken())};
        headers.push(header);

        return new FileUploader(
            {
                url: options.url,
                method: options.method,
                headers,
                allowedFileType: options.allowedMimeType
            });
    }
}

export interface UploadOptions {
    method: string;
    url: string;
    queueLimit?: number;
    allowedMimeType?: string[];

    methodAfterSuccess?(item?: FileItem, response?: string, status?: number, headers?: ParsedResponseHeaders): any;
}
