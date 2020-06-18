import { TestBed } from '@angular/core/testing';
import { ShareDataService } from './share-data.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('ShareDataService', () => {
    let shareDataService = ShareDataService;
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [ShareDataService],
        });
        shareDataService = TestBed.get(ShareDataService);
    });

    it('should be created', () => {
        const service: ShareDataService = TestBed.get(ShareDataService);
        expect(service).toBeTruthy();
    });
});
