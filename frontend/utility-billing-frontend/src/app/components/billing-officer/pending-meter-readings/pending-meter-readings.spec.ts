import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingMeterReadings } from './pending-meter-readings';

describe('PendingMeterReadings', () => {
  let component: PendingMeterReadings;
  let fixture: ComponentFixture<PendingMeterReadings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PendingMeterReadings]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PendingMeterReadings);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
