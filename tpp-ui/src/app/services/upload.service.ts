import {Injectable} from '@angular/core';
import {AbstractServices, ResultListResponse} from './abstract-services';
import {environment} from '@environments/environment';
import 'rxjs/add/operator/map';
import {FileUploader} from 'ng2-file-upload';
import {AuthenticationService} from '@services/authentication.service';
import {TranslateService} from '@ngx-translate/core';
import {HttpClient} from '@angular/common/http';
import {Document} from '@models/document.model';
import {APP_CONSTANTS} from '@common/constants/app.constants';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';
import {ParsedResponseHeaders} from 'ng2-file-upload/file-upload/file-uploader.class';

@Injectable()
export class UploadService {

    constructor(protected http: HttpClient) {
    }

    createInstanceFileUploader(options: UploadOptions): FileUploader {
        // Set the current user credentials in Headers for Basic Authorization
        const headers = [];
        const token = this.authService.getUserToken();
        if (token) {
            const header = {name: 'Authorization', value: 'Basic ' + token};
            headers.push(header);
        }

        return new FileUploader(
            {
                queueLimit: options.queueLimit ? options.queueLimit : 1,
                url: options.url,
                method: options.method,
                headers,
                maxFileSize: options.maxFileSize ? options.maxFileSize : APP_CONSTANTS.UPLOAD.defaultMaxFileSize,
                allowedMimeType: options.allowedMimeType ? options.allowedMimeType : APP_CONSTANTS.UPLOAD.mimeTypeOnlyImage
            });
    }

}

export interface UploadOptions {
    method: string;
    url: string;
    queueLimit?: number;
    maxFileSize?: number;
    allowedMimeType: string[];
    context?: string;

    methodAfterSuccess(item?: FileItem, response?: string, status?: number, headers?: ParsedResponseHeaders): any;
}
