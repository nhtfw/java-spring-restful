package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.hoidanit.jobhunter.domain.response.file.ResUploadFileDTO;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.StorageException;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Autowired
    private FileService FileService;

    @Value("${hoidanit.upload-file.base-uri}")
    private String baseURI;

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> uploadFile(
            // RequestBody để lấy Json ở raw
            // RequestParam để lấy parameter(ở đây là lấy ở form-data)
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder)
            throws URISyntaxException, IOException, StorageException {

        // check validate
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        /*
         * forech, check fileName (viết thường - lower case) có chứa 1 trong các hậu tố
         * mở rộng ở trên hay không
         */
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException("Invalid file extention. Only allows " + allowedExtensions.toString());
        }

        // create a directory if not exist
        this.FileService.createDirectory(baseURI + folder);

        // store file
        String uploadFile = this.FileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }
}
