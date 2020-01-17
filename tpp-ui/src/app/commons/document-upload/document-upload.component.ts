import {Component, Input, OnInit} from '@angular/core';
import {FileItem, FileUploader} from 'ng2-file-upload';
import {UploadOptions, UploadService} from '../../services/upload.service';
import {InfoService} from "../info/info.service";
import {SpinnerVisibilityService} from "ng-http-loader";
import {error} from "util";

// Increasing integer for generating unique ids for document-upload components.
let nextUniqueId = 0;

@Component({
    selector: 'app-document-upload',
    templateUrl: './document-upload.component.html',
    styleUrls: ['./document-upload.component.scss']
})
export class DocumentUploadComponent implements OnInit {
    private _uniqueId = `document-upload-${++nextUniqueId}`;

    // Object for uploading
    public uploader: FileUploader;

    /** A unique id for the document-upload input. If none is supplied, it will be auto-generated. */
    @Input()
    id: string = this._uniqueId;

    // Options for uploading
    @Input() options: UploadOptions;

    hasBaseDropZoneOver: boolean = true;

    constructor(private uploadService: UploadService,
                private spinner: SpinnerVisibilityService,
                private infoService: InfoService) {
    }

    public get acceptedMimes(): string {
        return (this.options && this.options.allowedMimeType)
            ? this.options.allowedMimeType.join(',')
            : null;
    }

    public ngOnInit(): void {
        this.uploader = this.uploadService.createInstanceFileUploader(this.options);

        /* Ensure again that the number of up-to-load file is always one and get the image path for preview */
        this.uploader.onAfterAddingFile = (item) => this.onAfterAddingFile(item);

        this.uploader.onProgressAll = (progress) => {
            this.spinner.show();
        };

        this.uploader.response.subscribe(() => {
            this.spinner.hide();
        });

        this.uploader.onCompleteItem = (item: FileItem, response: string, status, headers) => {
            if (this.options.methodAfterSuccess && typeof this.options.methodAfterSuccess === 'function') {
                this.options.methodAfterSuccess(item, response);
            }
            if (status != 200 && status != 201) {
              this.infoService.openFeedback("File was not uploaded. Check your file, please!");
            } else {
              this.infoService.openFeedback("File successfully uploaded");
            }
        };

        this.uploader.onWhenAddingFileFailed = (item, filter, options) => {
            if (filter.name === 'mimeType' || filter.name === 'fileSize') {
                let extensions = '';
                if (this.options.allowedMimeType) {
                    this.options.allowedMimeType.forEach((extension: string) => {
                        extensions = extensions + extension.split('/').pop() + ', ';
                    });
                }
                const params: any = {
                    file: item.name,
                    extensions: extensions
                };
                const message: string = 'ERROR UPLOAD' + filter.name;
                this.infoService.openFeedback(message, {severity: "error"});
            }
        };
    }

    fileOverBase(e: any): void {
        this.hasBaseDropZoneOver = e;
    }

    onAfterAddingFile(item: FileItem): void {
        if (this.options.queueLimit === 1) {
            if (this.uploader.queue.length > 1) {
                this.uploader.removeFromQueue(this.uploader.queue[0]);
            }
        }
    }
}
