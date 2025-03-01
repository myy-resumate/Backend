package dev.resumate.service;

import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.domain.Tag;
import dev.resumate.domain.Tagging;
import dev.resumate.repository.TagRepository;
import dev.resumate.repository.TaggingRepository;
import dev.resumate.repository.dto.TagDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

}
