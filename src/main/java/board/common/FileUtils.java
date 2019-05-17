package board.common;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import board.board.dto.BoardFileDto;

@Component
public class FileUtils {
	
	public List<BoardFileDto> parseFileInfo(int boardIdx, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {
		if(ObjectUtils.isEmpty(multipartHttpServletRequest)) {
			return null;
		}
		
		List<BoardFileDto> fileList = new ArrayList<>();
		
		// 파일이 업로드 될 때마다 images 폴더 밑에 yyyyMMdd 형식으로 폴더 생성(해당 폴더가 없을 경우만)
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
		ZonedDateTime current = ZonedDateTime.now();
		String path = "images/" + current.format(format);
		File file = new File(path);
		
		if(file.exists() == false) {
			file.mkdirs();
		}
		
		Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
		
		String newFileName, originalFileExtension, contentType;
		
		while(iterator.hasNext()) {
			List<MultipartFile> list = multipartHttpServletRequest.getFiles(iterator.next());
			for(MultipartFile multipartFile : list) {
				if(multipartFile.isEmpty() == false) {
					contentType = multipartFile.getContentType();	// 파일 형식을 확인하고 이미지 확장자 지정
					// 파일의 확장자를 파일 이름에서 가져오는 방식은 매우 위험.
					if(ObjectUtils.isEmpty(contentType)) {
						break;
					} else {
						if(contentType.contains("image/jpeg")) {
							originalFileExtension = ".jpg";
						} else if(contentType.contains("image/png")) {
							originalFileExtension = ".png";
						} else if(contentType.contains("image/gif")) {
							originalFileExtension = ".gif";
						} else {
							break;
						}
					}
					
					// 서버에 저장될 파일 이름 생성
					newFileName = Long.toString(System.nanoTime()) + originalFileExtension;
					
					// 데이터베이스에 저장할 파일 정보를  BoardFileDto에 저장
					BoardFileDto boardFile = new BoardFileDto();
					boardFile.setBoardIdx(boardIdx);	// 어떤 게시글에 속해 있는지
					boardFile.setFileSize(multipartFile.getSize());
					boardFile.setOriginalFileName(multipartFile.getOriginalFilename());
					boardFile.setStoredFilePath(path + "/" + newFileName);
					fileList.add(boardFile);
					
					// 업로드된 파일을 새로운 이름으로 바꾸어 지정된 경로에 저장
					file = new File(path + "/" + newFileName);
					multipartFile.transferTo(file);
				}
			}
		}
		return fileList;
	}
}
