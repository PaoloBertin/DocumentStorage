package eu.opensource.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import eu.opensource.model.Document;
import eu.opensource.repository.DocumentRepository;
import eu.opensource.services.DocumentService;

/**
 * Service implentation for entity Document
 * 
 * @author Paolo Bertin
 *
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service("documentService")
public class DocumentServiceImpl implements DocumentService {

	private final DocumentRepository  DocumentRepository;
	

}