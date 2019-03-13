import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';
import {ParsedResponseHeaders} from 'ng2-file-upload/file-upload/file-uploader.class';
import 'rxjs/add/operator/map';


@Injectable({
    providedIn: 'root'
})
export class UploadService {

    constructor(protected http: HttpClient) {
    }

    createInstanceFileUploader(options: UploadOptions): FileUploader {
        // Set the current user credentials in Headers for Basic Authorization
        // TODO CHECK IF AUTH NEEDED
        const headers = [];
        const token = 'xxxx';
        if (token) {
            /* TODO CHECK IF AUTH NEEDED
            const header = {name: 'Authorization', value: 'Basic ' + token};
            headers.push(header);
            */
        }

        return new FileUploader(
            {
                queueLimit: options.queueLimit ? options.queueLimit : 1,
                url: options.url,
                method: options.method,
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
