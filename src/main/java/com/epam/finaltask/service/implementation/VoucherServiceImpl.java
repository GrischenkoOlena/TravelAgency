package com.epam.finaltask.service.implementation;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.OrderRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;

import com.epam.finaltask.service.VoucherService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    public static final String ERROR_MESSAGE = "Voucher not found with id ";
    private final VoucherRepository voucherRepo;
    private final VoucherMapper voucherMapper;
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;

    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        Voucher newVoucher = voucherMapper.toVoucher(voucherDTO);
        return voucherMapper.toVoucherDTO(voucherRepo.save(newVoucher));
    }

    @Override
    public VoucherDTO order(String id, String userId) {
        Voucher existingVoucher = voucherRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException(ERROR_MESSAGE + id));
        User existingUser = userRepo.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        Order order = Order.builder()
                .user(existingUser)
                .voucher(existingVoucher)
                .status(VoucherStatus.REGISTERED)
                .build();
        orderRepo.save(order);

        Voucher updatedVoucher = voucherRepo.findById(UUID.fromString(id)).orElseThrow();
        return voucherMapper.toVoucherDTO(updatedVoucher);
    }


    @Override
    @Transactional
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        Voucher existingVoucher = voucherRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException(ERROR_MESSAGE + id));

        voucherMapper.updateEntityFromDto(voucherDTO, existingVoucher);
        return voucherMapper.toVoucherDTO(voucherRepo.save(existingVoucher));
    }

    @Override
    public void delete(String voucherId) {
        Voucher voucher = voucherRepo
                .findById(UUID.fromString(voucherId))
                .orElseThrow(() -> new EntityNotFoundException(ERROR_MESSAGE + voucherId));
        voucherRepo.delete(voucher);
    }

    @Override
    public VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO) {
        Voucher existingVoucher = voucherRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException(ERROR_MESSAGE + id));

        existingVoucher.setHot(voucherDTO.getIsHot());
        return voucherMapper.toVoucherDTO(voucherRepo.save(existingVoucher));
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        return voucherRepo.findAllByUserId(UUID.fromString(userId))
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByTourType(TourType tourType) {
        return voucherRepo.findAllByTourType(tourType)
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        return voucherRepo.findAllByTransferType(TransferType.valueOf(transferType))
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByPrice(Double price) {
        return voucherRepo.findAllByPrice(price)
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(HotelType hotelType) {
        return voucherRepo.findAllByHotelType(hotelType)
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAll() {
        return voucherRepo.findAll()
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public VoucherDTO findById(String id) {
        return voucherMapper.toVoucherDTO(voucherRepo.findById(UUID.fromString(id)).orElseThrow());
    }
}
