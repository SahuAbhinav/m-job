/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.batch.configuration;

import javax.batch.api.chunk.listener.ChunkListener;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * @author Michael Minella
 */
public class MyChunkListener implements ChunkListener {


    @Override
    public void beforeChunk() throws Exception {

        System.out.println(">> Before the chunk");
        
    }

    @Override
    public void onError(Exception ex) throws Exception {

        System.out.println("On Error");
        
    }

    @Override
    public void afterChunk() throws Exception {

        System.out.println("<< After the chunk");
        
    }
}
