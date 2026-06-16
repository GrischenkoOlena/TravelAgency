package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.RemoteResponse;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.VoucherService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@AllArgsConstructor
public class VoucherRestController {
    private final VoucherService voucherService;

    @GetMapping
    public ResponseEntity<RemoteResponse<VoucherDTO>> findAllVouchers(){
        RemoteResponse<VoucherDTO> response = RemoteResponse.success(voucherService.findAll(), null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<RemoteResponse<VoucherDTO>> findVouchersByUserId(@PathVariable String userId){
        List<VoucherDTO> vouchers = voucherService.findAllByUserId(userId);
        RemoteResponse<VoucherDTO> response = RemoteResponse.success(vouchers, null);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse<VoucherDTO>> createVoucher(@RequestBody VoucherDTO voucherDTO){
        VoucherDTO newVoucher = voucherService.create(voucherDTO);
        RemoteResponse<VoucherDTO> response = RemoteResponse.success(List.of(newVoucher),
                "Voucher is successfully created");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{voucherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse<VoucherDTO>> updateVoucher(@PathVariable String voucherId, @RequestBody VoucherDTO voucherDTO){
        VoucherDTO updateVoucher = voucherService.update(voucherId, voucherDTO);
        RemoteResponse<VoucherDTO> response = RemoteResponse.success(List.of(updateVoucher),
                "Voucher is successfully updated");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{voucherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse<VoucherDTO>> deleteVoucher(@PathVariable String voucherId){
        voucherService.delete(voucherId);
        RemoteResponse<VoucherDTO> response = RemoteResponse.success(List.of(),
                String.format("Voucher with Id %s has been deleted", voucherId));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{voucherId}/status")
    @PreAuthorize("hasRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RemoteResponse<VoucherDTO>> changeVoucherStatus(@PathVariable String voucherId, @RequestBody VoucherDTO voucherDTO){
        VoucherDTO updateVoucher = voucherService.changeHotStatus(voucherId, voucherDTO);
        RemoteResponse<VoucherDTO> response = RemoteResponse.success(List.of(updateVoucher),
                "Voucher status is successfully changed");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{voucherId}/order")
    public ResponseEntity<RemoteResponse<VoucherDTO>> orderVoucher(@PathVariable String voucherId, @RequestBody String userId){
        VoucherDTO updateVoucher = voucherService.order(voucherId, userId);
        RemoteResponse<VoucherDTO> response = RemoteResponse.success(List.of(updateVoucher),
                "Voucher is successfully ordered");
        return ResponseEntity.ok(response);
    }
	
}
