import {Component, Input, OnInit} from '@angular/core';
import {UploadOptions, UploadService} from '@services/upload.service';
import {FileItem, FileUploader} from 'ng2-file-upload';
import {MessagesService} from '@services/messages.service';
import {TranslateService} from '@ngx-translate/core';
import * as _ from 'lodash';

@Component({
    selector: 'app-document-upload',
    templateUrl: './document-upload.component.html',
    styleUrls: ['./document-upload.component.scss']
})
export class DocumentUploadComponent implements OnInit {

    // Object for uploading
    uploader: FileUploader;

    // Options for uploading
    @Input() options: UploadOptions;

    hasBaseDropZoneOver: boolean = true;

    constructor(private uploadService: UploadService) {
    }

    public get acceptedMimes(): string {
        return (this.options && this.options.allowedMimeType)
            ? this.options.allowedMimeType.join(',')
            : null;
    }

    ngOnInit() {
        this.uploader = this.uploadService.createInstanceFileUploader(this.options);

        /* Ensure again that the number of up-to-load file is always one and get the image path for preview */
        this.uploader.onAfterAddingFile = (item) => this.onAfterAddingFile(item);

        this.uploader.onCompleteItem = (item: FileItem, response: string, status, headers) => {
            if (this.options.methodAfterSuccess && typeof this.options.methodAfterSuccess === 'function') {
                this.options.methodAfterSuccess(item, response);
            }
            this.messageService.success('COMMON.lblSuccesfullMessage');
        };

        this.uploader.onWhenAddingFileFailed = (item, filter, options) => {
            if (filter.name === 'mimeType' || filter.name === 'fileSize') {
                let extensions: string = '';
                if (this.options.allowedMimeType) {
                    this.options.allowedMimeType.forEach((extension: string) => {
                        extensions = extensions + extension.split('/').pop() + ', ';
                    });
                }
                const params: any = {
                    file: item.name,
                    sizeLimit: (`${Math.round(this.options.maxFileSize / 1024)} KB`),
                    extensions: extensions
                };
                const message: string = 'ERROR.lblMessage' + filter.name;
                this.messageService.error(this.translateService.instant(message, params));
            }
        };
    }

    fileOverBase(e: any): void {
        this.hasBaseDropZoneOver = e;
    }

    onAfterAddingFile(item: FileItem): void {
        if (this.options.maxFileSize === 1) {
            if (this.uploader.queue.length > 1) {
                this.uploader.removeFromQueue(this.uploader.queue[0]);
            }
        }
        // Generate image preview path
        const filePreviewPath = this.sanitizer.bypassSecurityTrustUrl((window.URL.createObjectURL(item._file)));
        _.extend(item, {filePreviewPath: filePreviewPath});
    }

}
