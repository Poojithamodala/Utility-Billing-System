import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApprovedConsumers } from './approved-consumers';

describe('ApprovedConsumers', () => {
  let component: ApprovedConsumers;
  let fixture: ComponentFixture<ApprovedConsumers>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApprovedConsumers]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ApprovedConsumers);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
