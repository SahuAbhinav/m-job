package com.proj.batch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.proj.domain.Member;

public class MemberItemProcessor2 implements ItemProcessor<Member, Member> {

    private static final Logger log = LoggerFactory.getLogger(MemberItemProcessor2.class);
    
    @Override
    public Member process(final Member person) throws Exception {
        final String firstName = person.getFirstName().toLowerCase();
        final String lastName = person.getLastName().toLowerCase();

        final Member transformedPerson = new Member(firstName, lastName);

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }
}
