


package eu.opensource.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

	private final DocumentRepository  documentRepository;

	@Override
	public Optional<Document> getDocumentById(Long id) {

		return  documentRepository.findById(id);
	}

	@Override
    public Page<Document> getAllDocument(Pageable pageable){

		log.info("Fetching all Documents");
	
        return documentRepository.findAll(pageable);
	}

	@Override
    public Document saveDocument(Document document){

		log.info("Saving Document: {}", document);

		return documentRepository.save(document);
	}

	@Override
    public void deleteById(Long id) {

		log.info("Deleting Document with id: {}", id);

		documentRepository.deleteById(id);
		
	}
}