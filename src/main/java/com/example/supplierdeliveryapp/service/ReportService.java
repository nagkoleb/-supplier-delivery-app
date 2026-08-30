package com.example.supplierdeliveryapp.service;

import com.example.supplierdeliveryapp.dto.DeliveryReportResponse;
import com.example.supplierdeliveryapp.exception.BadRequestException;
import com.example.supplierdeliveryapp.repository.DeliveryItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final DeliveryItemRepository deliveryItemRepository;

    public ReportService(DeliveryItemRepository deliveryItemRepository) {
        this.deliveryItemRepository = deliveryItemRepository;
    }

    public List<DeliveryReportResponse> getReport(
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        if (dateFrom.isAfter(dateTo)) {
            throw new BadRequestException(
                    "Дата начала периода не может быть позже даты окончания"
            );
        }
        return deliveryItemRepository.findReportByPeriod(dateFrom, dateTo);
    }
}