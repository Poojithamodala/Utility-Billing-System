import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectionRequests } from './connection-requests';

describe('ConnectionRequests', () => {
  let component: ConnectionRequests;
  let fixture: ComponentFixture<ConnectionRequests>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConnectionRequests]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConnectionRequests);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
